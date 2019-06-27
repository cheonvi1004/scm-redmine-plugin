package sonia.scm.redmine.update;

import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.config.RedmineGlobalConfiguration;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.version.Version;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Optional;

import static sonia.scm.version.Version.parse;

@Extension
public class RedmineV2GlobalConfigMigrationUpdateStep implements UpdateStep {

  public static final String STORE_NAME = "redmine";
  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public RedmineV2GlobalConfigMigrationUpdateStep(ConfigurationStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    Optional<V1RedmineGlobalConfiguration> optionalConfig = storeFactory.withType(V1RedmineGlobalConfiguration.class).withName(STORE_NAME).build().getOptional();
    if (isV1Config(optionalConfig)) {
      optionalConfig.ifPresent(
          v1RedmineConfig -> {
            RedmineGlobalConfiguration v2RedmineConfig = new RedmineGlobalConfiguration(
              v1RedmineConfig.getUrl(),
              v1RedmineConfig.getTextFormatting(),
              v1RedmineConfig.isAutoClose(),
              v1RedmineConfig.isUpdateIssues(),
              v1RedmineConfig.isDisableRepositoryConfiguration(),
              "",
              ""
            );
            storeFactory.withType(RedmineGlobalConfiguration.class).withName(STORE_NAME).build().set(v2RedmineConfig);
          }
        );
    }
  }

  private boolean isV1Config(Optional<V1RedmineGlobalConfiguration> optionalConfig) {
    if (optionalConfig.isPresent()) {
      try {
        return optionalConfig.get() instanceof V1RedmineGlobalConfiguration;
      } catch (ClassCastException e) {
        return true;
      }
    } else {
      return false;
    }
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.redmine.config.global.xml";
  }

  @XmlRootElement(name = "redmine")
  @XmlAccessorType(XmlAccessType.FIELD)
  static class V1RedmineGlobalConfiguration extends RedmineGlobalConfiguration {
  }
}
