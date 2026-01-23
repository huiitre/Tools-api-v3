package fr.huiitre.tools.config.dofus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.modules.dofus.almanax.infrastructure.Dofus3AlmanaxDataProvider;
import fr.huiitre.tools.modules.dofus.assets.application.ports.AlmanaxDataProvider;
import fr.huiitre.tools.modules.dofus.assets.application.ports.ItemDataProvider;
import fr.huiitre.tools.modules.dofus.assets.application.ports.ItemTypeDataProvider;
import fr.huiitre.tools.modules.dofus.assets.infrastructure.dofus3.Dofus3ItemDataProvider;
import fr.huiitre.tools.modules.dofus.assets.infrastructure.dofus3.Dofus3ItemTypeDataProvider;
import fr.huiitre.tools.modules.dofus.assets.infrastructure.dofus3.Dofus3LanguageDataProviderImpl;
import fr.huiitre.tools.modules.dofus.assets.infrastructure.dofus3.Dofus3LocalAssetsReader;
import fr.huiitre.tools.modules.dofus.sync.application.ports.Dofus3LanguageDataProvider;

@Configuration
public class DofusSyncConfig {

    @Bean
    public Dofus3LocalAssetsReader dofus3LocalAssetsReader() {
        return new Dofus3LocalAssetsReader();
    }

    @Bean
    public Dofus3LanguageDataProvider dofus3LanguageDataProvider(
            Dofus3LocalAssetsReader assetsReader) {
        return new Dofus3LanguageDataProviderImpl(assetsReader);
    }

    @Bean
    public ItemTypeDataProvider itemTypeDataProvider(
            Dofus3LocalAssetsReader assetsReader,
            Dofus3LanguageDataProvider languageDataProvider) {
        return new Dofus3ItemTypeDataProvider(assetsReader, languageDataProvider);
    }

    @Bean
    public ItemDataProvider itemDataProvider(
            Dofus3LocalAssetsReader assetsReader,
            Dofus3LanguageDataProvider languageDataProvider) {
        return new Dofus3ItemDataProvider(assetsReader, languageDataProvider);
    }

    @Bean
    public AlmanaxDataProvider almanaxDataProvider(
            Dofus3LocalAssetsReader assetsReader,
            Dofus3LanguageDataProvider languageDataProvider) {
        return new Dofus3AlmanaxDataProvider(assetsReader, languageDataProvider);
    }
}
