package fr.huiitre.tools.modules.dofus.sync.application.usecase;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.dofus.sync.application.usecase.item.SyncItemUseCase;
import fr.huiitre.tools.modules.dofus.sync.application.usecase.itemtype.SyncItemTypesUseCase;
import fr.huiitre.tools.modules.core.mail.infrastructure.MailSenderService;
import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.report.infrastructure.ReportFileGenerator;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.dofus.game.application.view.GameVersionData;
import fr.huiitre.tools.modules.dofus.sync.application.ports.Dofus3LanguageDataProvider;
import fr.huiitre.tools.modules.dofus.sync.application.usecase.almanax.SyncAlmanaxUseCase;

@Service
@Transactional
public class SyncDofus3DataUseCase implements SecuredUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SyncDofus3DataUseCase.class);

    private static final int INLINE_LIMIT = 100;
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Dofus3LanguageDataProvider languageDataProvider;

    private final MailSenderService mailSenderService;
    private final ReportFileGenerator reportFileGenerator;

    private final SyncItemTypesUseCase syncItemTypesUseCase;
    private final SyncItemUseCase syncItemUseCase;
    private final SyncAlmanaxUseCase syncAlmanaxUseCase;

    public SyncDofus3DataUseCase(
            ReportFileGenerator reportFileGenerator,
            MailSenderService mailSenderService,
            SyncItemTypesUseCase syncItemTypesUseCase,
            SyncItemUseCase syncItemDataUseCase,
            Dofus3LanguageDataProvider languageDataProvider,
            SyncAlmanaxUseCase syncAlmanaxUseCase) {
        this.reportFileGenerator = reportFileGenerator;
        this.mailSenderService = mailSenderService;
        this.syncItemTypesUseCase = syncItemTypesUseCase;
        this.syncItemUseCase = syncItemDataUseCase;
        this.languageDataProvider = languageDataProvider;
        this.syncAlmanaxUseCase = syncAlmanaxUseCase;
    }

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public void execute(GameVersionData gameVersion) {

        languageDataProvider.reload();

        // =====================================================
        // ITEM TYPES
        // =====================================================
        SyncReport itemTypeReport = syncItemTypesUseCase.execute(gameVersion);

        // =====================================================
        // ITEMS
        // =====================================================
        SyncReport itemReport = syncItemUseCase.execute(gameVersion);

        // =====================================================
        // ITEMS
        // =====================================================
        SyncReport almanaxReport = syncAlmanaxUseCase.execute(gameVersion);

        // =====================================================
        // FUTUR : AUTRES JSON
        // XxxSyncReport xxxReport = syncXxxDataUseCase.execute(...);
        // =====================================================

        List<Attachment> attachments = new ArrayList<>();

        StringBuilder body = new StringBuilder();
        body.append("[DOFUS3][SYNC] Rapport de synchronisation\n\n");
        body.append("Date              : ").append(LocalDateTime.now().format(TS)).append('\n');
        body.append("GameVersionId     : ").append(gameVersion.getId()).append('\n');
        body.append("GameVersionCode   : ").append(gameVersion.getCode()).append("\n\n");

        int globalCreated = 0;
        int globalUpdated = 0;

        // ------------- ITEM TYPES -------------
        globalCreated += itemTypeReport.createdCount();
        globalUpdated += itemTypeReport.updatedCount();

        body.append(itemTypeReport.label()).append('\n');
        body.append("Ajouts        : ").append(itemTypeReport.createdCount()).append('\n');
        body.append("Modifications : ").append(itemTypeReport.updatedCount()).append('\n');

        if (itemTypeReport.totalChanges() == 0) {
            body.append("Détails       : aucun changement\n\n");
        } else if (itemTypeReport.totalChanges() <= INLINE_LIMIT) {
            body.append('\n').append(itemTypeReport.toInlineDetails()).append('\n');
        } else {
            String filename = buildAttachmentFilename(gameVersion, itemTypeReport.code());
            Path file = reportFileGenerator.generate(filename,
                    buildAttachmentHeader(gameVersion, itemTypeReport) + "\n" + itemTypeReport.toAttachmentContent());
            attachments.add(new Attachment(filename, file));
            body.append("Détails       : voir pièce jointe \"").append(filename).append("\"\n\n");
        }

        // ------------- ITEMS -------------
        globalCreated += itemReport.createdCount();
        globalUpdated += itemReport.updatedCount();

        body.append(itemReport.label()).append('\n');
        body.append("Ajouts        : ").append(itemReport.createdCount()).append('\n');
        body.append("Modifications : ").append(itemReport.updatedCount()).append('\n');

        if (itemReport.totalChanges() == 0) {
            body.append("Détails       : aucun changement\n\n");
        } else if (itemReport.totalChanges() <= INLINE_LIMIT) {
            body.append('\n').append(itemReport.toInlineDetails()).append('\n');
        } else {
            String filename = buildAttachmentFilename(gameVersion, itemReport.code());
            Path file = reportFileGenerator.generate(
                    filename,
                    buildAttachmentHeader(gameVersion, itemReport)
                            + "\n"
                            + itemReport.toAttachmentContent());
            attachments.add(new Attachment(filename, file));
            body.append("Détails       : voir pièce jointe \"")
                    .append(filename)
                    .append("\"\n\n");
        }

        // ------------- ALMANAX -------------
        globalCreated += almanaxReport.createdCount();
        globalUpdated += almanaxReport.updatedCount();

        body.append(almanaxReport.label()).append('\n');
        body.append("Ajouts        : ").append(almanaxReport.createdCount()).append('\n');
        body.append("Modifications : ").append(almanaxReport.updatedCount()).append('\n');

        if (almanaxReport.totalChanges() == 0) {
            body.append("Détails       : aucun changement\n\n");
        } else if (almanaxReport.totalChanges() <= INLINE_LIMIT) {
            body.append('\n').append(almanaxReport.toInlineDetails()).append('\n');
        } else {
            String filename = buildAttachmentFilename(gameVersion, almanaxReport.code());
            Path file = reportFileGenerator.generate(
                    filename,
                    buildAttachmentHeader(gameVersion, almanaxReport)
                            + "\n"
                            + almanaxReport.toAttachmentContent());
            attachments.add(new Attachment(filename, file));
            body.append("Détails       : voir pièce jointe \"")
                    .append(filename)
                    .append("\"\n\n");
        }

        // ------------- TOTAL -------------
        int total = globalCreated + globalUpdated;

        logger.info("[DOFUS3][SYNC] created={}, updated={}", globalCreated, globalUpdated);

        String subject = (total == 0)
                ? "[DOFUS3][SYNC][OK] Aucun changement"
                : "[DOFUS3][SYNC] +" + globalCreated + " ~" + globalUpdated;

        // si tu n’as aucune PJ -> simple mail
        if (attachments.isEmpty()) {
            mailSenderService.send(subject, body.toString());
            return;
        }

        // sinon -> mail + PJ multiples
        // NOTE: il te faut une méthode sendWithAttachments(List<Path>) côté
        // MailSenderService
        logger.info("SUBJECT = {}", subject);
        mailSenderService.sendWithAttachments(
                subject,
                body.toString(),
                attachments.stream().map(Attachment::path).toList());
    }

    private String buildAttachmentFilename(GameVersionData gameVersion, String reportCode) {
        // 1 fichier par JSON traité
        return "dofus3_sync_" + reportCode + "_gv" + gameVersion.getId() + ".txt";
    }

    private String buildAttachmentHeader(GameVersionData gameVersion, SyncReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("[DOFUS3][SYNC] Détails : ").append(report.label()).append("\n\n");
        sb.append("Date              : ").append(LocalDateTime.now().format(TS)).append('\n');
        sb.append("GameVersionId     : ").append(gameVersion.getId()).append('\n');
        sb.append("GameVersionCode   : ").append(gameVersion.getCode()).append('\n');
        sb.append("Ajouts            : ").append(report.createdCount()).append('\n');
        sb.append("Modifications     : ").append(report.updatedCount()).append('\n');
        return sb.toString();
    }

    private record Attachment(String name, Path path) {
    }
}
