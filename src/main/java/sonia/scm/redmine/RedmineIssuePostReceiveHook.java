/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
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
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.redmine;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryHook;
import sonia.scm.repository.RepositoryHookEvent;
import sonia.scm.repository.RepositoryHookType;
import sonia.scm.util.IOUtil;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Marvin Froeder marvin_at_marvinformatics_dot_com
 */
@Extension
public class RedmineIssuePostReceiveHook implements RepositoryHook
{

  /** Field description */
  public static final Collection<RepositoryHookType> TYPES =
    Arrays.asList(RepositoryHookType.POST_RECEIVE);

  /** the logger for RedmineIssuePostReceiveHook */
  private static final Logger logger =
    LoggerFactory.getLogger(RedmineIssuePostReceiveHook.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param requestFactory
   * @param templateHandler
   */
  @Inject
  public RedmineIssuePostReceiveHook(RedmineIssueRequestFactory requestFactory,
                                  TemplateHandler templateHandler)
  {
    this.requestFactory = requestFactory;
    this.templateHandler = templateHandler;
    this.changesetPreProcessorFactory = new RedmineChangesetPreProcessorFactory();
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param event
   */
  @Override
  public void onEvent(RepositoryHookEvent event)
  {
    Repository repository = event.getRepository();

    if (repository != null)
    {
      RedmineConfiguration configuraiton = new RedmineConfiguration(repository);

      if (configuraiton.isUpdateIssuesEnabled())
      {
        handleIssueEvent(event, repository, configuraiton);
      }
      else if (logger.isTraceEnabled())
      {
        logger.trace("redmine auto-close is disabled");
      }
    }
    else if (logger.isErrorEnabled())
    {
      logger.error("receive repository hook without repository");
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
  public Collection<RepositoryHookType> getTypes()
  {
    return TYPES;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public boolean isAsync()
  {
    return false;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param event
   * @param repository
   * @param configuration
   */
  private void handleIssueEvent(RepositoryHookEvent event,
                                    Repository repository,
                                    RedmineConfiguration configuration)
  {
    Collection<Changeset> changesets = event.getChangesets();

    if (Util.isNotEmpty(changesets))
    {
      RedmineChangesetPreProcessor jcpp =
        changesetPreProcessorFactory.createPreProcessor(repository);
      RedmineIssueRequest request = null;

      try
      {
        request = requestFactory.createRequest(configuration, repository);
        jcpp.setRedmineIssueHandler(new RedmineIssueHandler(templateHandler,
                request));

        for (Changeset c : changesets)
        {
          jcpp.process(c);
        }
      }
      finally
      {
        IOUtil.close(request);
      }
    }
    else if (logger.isWarnEnabled())
    {
      logger.warn("receive repository hook without changesets");
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private RedmineChangesetPreProcessorFactory changesetPreProcessorFactory;

  /** Field description */
  private RedmineIssueRequestFactory requestFactory;

  /** Field description */
  private TemplateHandler templateHandler;
}
