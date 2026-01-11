package fr.huiitre.tools.config.dofus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.dofus.ports.providers.AlmanaxDataProvider;
import fr.huiitre.tools.application.dofus.ports.providers.Dofus3LanguageDataProvider;
import fr.huiitre.tools.application.dofus.ports.providers.ItemDataProvider;
import fr.huiitre.tools.application.dofus.ports.providers.ItemTypeDataProvider;
import fr.huiitre.tools.infrastructure.dofus.assets.dofus3.Dofus3AlmanaxDataProvider;
import fr.huiitre.tools.infrastructure.dofus.assets.dofus3.Dofus3ItemDataProvider;
import fr.huiitre.tools.infrastructure.dofus.assets.dofus3.Dofus3ItemTypeDataProvider;
import fr.huiitre.tools.infrastructure.dofus.assets.dofus3.Dofus3LanguageDataProviderImpl;
import fr.huiitre.tools.infrastructure.dofus.assets.dofus3.Dofus3LocalAssetsReader;

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
