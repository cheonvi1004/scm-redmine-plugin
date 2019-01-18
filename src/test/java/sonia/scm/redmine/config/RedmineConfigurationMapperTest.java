package sonia.scm.redmine.config;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RedmineConfigurationMapperTest {

  private URI baseUri = URI.create("http://example.com/base/");

  private URI expectedBaseUri;

  @Rule
  public ShiroRule shiro = new ShiroRule();

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ScmPathInfoStore scmPathInfoStore;

  @InjectMocks
  RedmineConfigurationMapperImpl mapper;

  @Before
  public void init() {
    when(scmPathInfoStore.get().getApiRestUri()).thenReturn(baseUri);
    expectedBaseUri = baseUri.resolve("v2/redmine/configuration/");
  }

  @Test
  @SubjectAware(username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldMapAttributesToDto() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration(), createRepository());
    assertEquals( "heartofgo.ld", dto.getUrl());
    assertEquals(TextFormatting.MARKDOWN, dto.getTextFormatting());
    assertTrue(dto.isAutoClose());
    assertFalse(dto.isUpdateIssues());
  }

  @Test
  @SubjectAware(username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldAddHalLinksToDto() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration(), createRepository());
    assertEquals(expectedBaseUri.toString() + "foo/bar", dto.getLinks().getLinkBy("self").get().getHref());
    assertEquals(expectedBaseUri.toString() + "foo/bar", dto.getLinks().getLinkBy("update").get().getHref());
  }

  @Test
  @SubjectAware(username = "unpriv",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldNotAddUpdateLinkToDtoIfNotPermitted() {
    RedmineConfigurationDto dto = mapper.map(createConfiguration(), createRepository());
    assertFalse(dto.getLinks().getLinkBy("update").isPresent());
  }

  @Test
  public void shouldMapAttributesFromDto() {
    RedmineConfiguration configuration = mapper.map(createDto());
    assertEquals( "heartofgo.ld", configuration.getUrl());
    assertEquals(TextFormatting.MARKDOWN, configuration.getTextFormatting());
    assertTrue(configuration.isAutoClose());
    assertFalse(configuration.isUpdateIssues());
  }

  @Test
  @SubjectAware(username = "trillian",
    password = "secret",
    configuration = "classpath:sonia/scm/redmine/shiro.ini"
  )
  public void shouldMapGlobalConfigurationAttributesToDto() {
    RedmineGlobalConfigurationDto dto = mapper.map(createGlobalConfiguration());
    assertFalse(dto.isDisableRepositoryConfiguration());
  }

  @Test
  public void shouldMapGlobalConfigurationDtoAttributesFromDto() {
    RedmineGlobalConfiguration configuration = mapper.map(createGlobalConfigurationDto());
    assertFalse(configuration.isDisableRepositoryConfiguration());
  }

  private RedmineConfiguration createConfiguration() {
    return new RedmineConfiguration("heartofgo.ld",
      TextFormatting.MARKDOWN,
      true,
      false,
      "user",
      "password");
  }

  private RedmineConfigurationDto createDto() {
    return new RedmineConfigurationDto("heartofgo.ld",
      TextFormatting.MARKDOWN,
      true,
      false,
      "user",
      "password");
  }

  private RedmineGlobalConfiguration createGlobalConfiguration() {
    RedmineGlobalConfiguration configuration = new RedmineGlobalConfiguration();
    configuration.setUrl("");
    configuration.setTextFormatting(TextFormatting.TEXTILE);
    configuration.setUpdateIssues(false);
    configuration.setDisableRepositoryConfiguration(false);
    return configuration;
  }

  private RedmineGlobalConfigurationDto createGlobalConfigurationDto() {
    RedmineGlobalConfigurationDto configuration = new RedmineGlobalConfigurationDto();
    configuration.setUrl("");
    configuration.setTextFormatting(TextFormatting.TEXTILE);
    configuration.setUpdateIssues(false);
    configuration.setDisableRepositoryConfiguration(false);
    return configuration;
  }

  private Repository createRepository() {
    return new Repository("42", "GIT", "foo", "bar");
  }
}
