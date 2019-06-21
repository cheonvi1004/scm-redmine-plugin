package sonia.scm.redmine.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.redmine.config.RedmineConfigStore;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.redmine.config.TextFormatting;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.version.Version;

import javax.inject.Inject;

import static sonia.scm.update.V1PropertyReader.REPOSITORY_PROPERTY_READER;
import static sonia.scm.version.Version.parse;

@Extension
public class RedmineV2ConfigMigrationUpdateStep implements UpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(RedmineV2ConfigMigrationUpdateStep.class);

  private final V1PropertyDAO v1PropertyDAO;
  private final RedmineConfigStore configStore;

  @Inject
  public RedmineV2ConfigMigrationUpdateStep(V1PropertyDAO v1PropertyDAO, RedmineConfigStore configStore) {
    this.v1PropertyDAO = v1PropertyDAO;
    this.configStore = configStore;
  }

  @Override
  public void doUpdate() {
    v1PropertyDAO
      .getProperties(REPOSITORY_PROPERTY_READER)
      .havingAnyOf("redmine.url", "redmine.text-formatting", "redmine.auto-close", "redmine.update-issues")
      .forEachEntry((key, properties) -> configStore.storeConfiguration(buildConfig(key, properties), key));
  }

  private RedmineConfiguration buildConfig(String repositoryId, V1Properties properties) {
    LOG.debug("migrating repository specific redmine configuration for repository id {}", repositoryId);
    return new RedmineConfiguration(
      properties.get("redmine.url"),
      properties.getEnum("redmine.text-formatting", TextFormatting.class).orElse(null),
      properties.getBoolean("redmine.auto-close").orElse(false),
      properties.getBoolean("redmine.update-issues").orElse(false),
      "",
      ""
    );
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.redmine.config.repository.xml";
  }
}
