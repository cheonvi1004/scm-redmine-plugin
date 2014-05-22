/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.issuetracker.ChangeStateHandler;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.redmine.config.RedmineConfiguration;
import sonia.scm.template.Template;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 *
 * @author Sebastian Sdorra
 */
public class RedmineChangeStateHandler extends RedmineHandler
  implements ChangeStateHandler
{

  /** Field description */
  private static final String TEMPLATE = "scm/template/changeState.mustache";

  /**
   * the logger for RedmineChangeStateHandler
   */
  private static final Logger logger =
    LoggerFactory.getLogger(RedmineChangeStateHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param templateEngineFactory
   * @param linkHandler
   * @param configuration
   * @param request
   */
  public RedmineChangeStateHandler(TemplateEngineFactory templateEngineFactory,
    LinkHandler linkHandler, RedmineConfiguration configuration,
    IssueRequest request)
  {
    super(templateEngineFactory, linkHandler, configuration, request);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueIdString
   * @param keyword
   */
  @Override
  public void changeState(String issueIdString, String keyword)
  {
    int issueId = parseIssueId(issueIdString);

    try
    {

      String comment = createComment(request, keyword);

      if (!Strings.isNullOrEmpty(comment))
      {
        logger.info("close issue {} by keyword {}", issueId, keyword);

        Issue issue = getManager().getIssueById(issueId);
        
        // TODO: this will only work with the default workflow
        issue.setStatusId(5);
        issue.setNotes(comment);
        getManager().update(issue);
      }
      else
      {
        logger.warn("generate comment for close is null or empty");
      }

    }
    catch (RedmineException ex)
    {
      throw Throwables.propagate(ex);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Iterable<String> getKeywords()
  {
    return configuration.getAutoCloseWords();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param engine
   *
   * @return
   *
   * @throws IOException
   */
  @Override
  protected Template loadTemplate(TemplateEngine engine) throws IOException
  {
    return engine.getTemplate(TEMPLATE);
  }
}
