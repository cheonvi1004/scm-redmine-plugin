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



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.soap.JiraSoapService;
import sonia.scm.jira.soap.RemoteAuthenticationException;
import sonia.scm.jira.soap.RemoteComment;
import sonia.scm.jira.soap.RemotePermissionException;

//~--- JDK imports ------------------------------------------------------------

import java.rmi.RemoteException;

import java.util.GregorianCalendar;
import java.util.logging.Level;

/**
 *
 * @author Sebastian Sdorra
 */
public class SoapJiraHandler implements JiraHandler
{

  /** the logger for SoapJiraHandler */
  private static final Logger logger =
    LoggerFactory.getLogger(SoapJiraHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param service
   * @param token
   * @param username
   */
  public SoapJiraHandler(JiraSoapService service, String token, String username)
  {
    this.service = service;
    this.token = token;
    this.username = username;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueId
   * @param comment
   *
   * @throws JiraException
   */
  @Override
  public void addComment(String issueId, String comment) throws JiraException
  {
    RemoteComment remoteComment = new RemoteComment();

    remoteComment.setAuthor(username);
    remoteComment.setCreated(new GregorianCalendar());
    remoteComment.setBody(comment);

    try
    {
      service.addComment(token, issueId, remoteComment);
    }
    catch (Exception ex)
    {
      throw new JiraException("add comment failed", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param issueId
   *
   * @throws JiraException
   */
  @Override
  public void close(String issueId) throws JiraException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Method description
   *
   *
   * @param issueId
   * @param comment
   *
   * @throws JiraException
   */
  @Override
  public void closeIssue(String issueId, String comment) throws JiraException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private JiraSoapService service;

  /** Field description */
  private String token;

  /** Field description */
  private String username;
}