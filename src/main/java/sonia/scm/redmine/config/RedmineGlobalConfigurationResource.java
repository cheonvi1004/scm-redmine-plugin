/***
 * Copyright (c) 2015, Sebastian Sdorra
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
 * https://bitbucket.org/sdorra/scm-manager
 *
 */

package sonia.scm.redmine.config;

import com.google.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.redmine.RedmineIssueTracker;
import sonia.scm.security.Role;

/**
 * @author Sebastian Sdorra
 */
@Path("plugins/redmine/global-config")
public class RedmineGlobalConfigurationResource {
  private RedmineIssueTracker tracker;


  private static final Logger logger =
    LoggerFactory.getLogger(RedmineGlobalConfigurationResource.class);

  @Inject
  public RedmineGlobalConfigurationResource(RedmineIssueTracker tracker) {
    if (!SecurityUtils.getSubject().hasRole(Role.ADMIN)) {
      logger.warn("user has not enough privileges to change global redmine configuration");

      throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    this.tracker = tracker;
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  public Response updateConfiguration(RedmineGlobalConfiguration updatedConfig) {
    tracker.setGlobalConfiguration(updatedConfig);

    return Response.ok().build();
  }


  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getConfiguration() {
    return Response.ok(tracker.getGlobalConfiguration()).build();
  }

}
