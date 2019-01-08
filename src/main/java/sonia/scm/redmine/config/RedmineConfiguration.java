/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
 */


package sonia.scm.redmine.config;


import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sonia.scm.PropertiesAware;
import sonia.scm.Validateable;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author Sebastian Sdorra
 * @author Marvin Froeder marvin_at_marvinformatics_dot_com
 */
@AllArgsConstructor
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class RedmineConfiguration implements Validateable {

  static final String PROPERTY_AUTOCLOSE = "redmine.auto-close";
  static final String PROPERTY_REDMINE_URL = "redmine.url";
  static final String PROPERTY_UPDATEISSUES = "redmine.update-issues";
  static final String PROPERTY_USERNAMETRANSFORMER = "redmine.auto-close-username-transformer";
  static final String PROPERTY_TEXT_FORMATTING = "redmine.text-formatting";

  private String url;
  private TextFormatting textFormatting = TextFormatting.TEXTILE;
  private String usernameTransformPattern = "{0}";
  private boolean autoClose;
  private boolean updateIssues;

  public RedmineConfiguration() {
  }

  public RedmineConfiguration(PropertiesAware propsAware) {
    url = propsAware.getProperty(PROPERTY_REDMINE_URL);
    updateIssues = getBooleanProperty(propsAware, PROPERTY_UPDATEISSUES);
    autoClose = getBooleanProperty(propsAware, PROPERTY_AUTOCLOSE);
    usernameTransformPattern = propsAware.getProperty(PROPERTY_USERNAMETRANSFORMER);
    String textFormattingProperty = propsAware.getProperty(PROPERTY_TEXT_FORMATTING);
    if (!Strings.isNullOrEmpty(textFormattingProperty)) {
      textFormatting = TextFormatting.valueOf(textFormattingProperty);
    }
  }

  public String getUsernameTransformPattern() {
    return usernameTransformPattern;
  }

  public boolean isAutoCloseEnabled() {
    return isUpdateIssuesEnabled() && autoClose;
  }

  public boolean isUpdateIssuesEnabled() {
    return isValid() && updateIssues;
  }

  @Override
  public boolean isValid() {
    return Util.isNotEmpty(url);
  }

  private boolean getBooleanProperty(PropertiesAware propsAware, String key) {
    boolean result = false;
    String value = propsAware.getProperty(key);

    if (Util.isNotEmpty(value)) {
      result = Boolean.parseBoolean(value);
    }

    return result;
  }
}
