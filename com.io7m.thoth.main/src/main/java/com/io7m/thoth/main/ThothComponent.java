/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.thoth.main;

import java.util.Objects;
import com.io7m.thoth.command.api.ThothResolverType;
import com.io7m.thoth.connection.ThothIRCConnection;
import com.io7m.thoth.connection.ThothIRCConnectionConfiguration;
import com.io7m.thoth.connection.ThothIRCConnectionType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main IRC bot component.
 */

@Component(
  immediate = true,
  configurationPid = "com.io7m.thoth.irc",
  configurationPolicy = ConfigurationPolicy.REQUIRE)
public final class ThothComponent
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ThothComponent.class);
  }

  private AtomicReference<ThothIRCConnectionType> connection;
  private Thread thread;
  private ThothResolverType resolver;

  /**
   * Construct a new IRC component
   */

  public ThothComponent()
  {
    this.connection = new AtomicReference<>();
  }

  /**
   * Register a resolver.
   *
   * @param in_resolver The resolver
   */

  @Reference(
    cardinality = ReferenceCardinality.MANDATORY,
    policy = ReferencePolicy.STATIC)
  public void onRegisterResolver(
    final ThothResolverType in_resolver)
  {
    this.resolver = Objects.requireNonNull(in_resolver, "Resolver");
  }

  /**
   * Activate the bot.
   *
   * @param configuration The configuration values
   *
   * @throws Exception On errors
   */

  @Activate
  public void onActivate(
    final ThothComponentConfigurationType configuration)
    throws Exception
  {
    LOG.debug("onActivate");

    this.thread = new Thread(() -> {
      try {
        final ThothIRCConnectionConfiguration config =
          ThothIRCConnectionConfiguration.builder()
            .setAddress(new InetSocketAddress(
              configuration.address(),
              configuration.port()))
            .setChannel(configuration.channel())
            .setUser(configuration.user())
            .build();

        final ThothIRCConnectionType c =
          ThothIRCConnection.create(config, this.resolver);
        this.connection.set(c);
        c.start();
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });

    this.thread.setName("ThothIRCConnection");
    this.thread.start();
  }

  /**
   * Deactivate the bot.
   *
   * @throws Exception On errors
   */

  @Deactivate
  public void onDeactivate()
    throws Exception
  {
    LOG.debug("onDeactivate");

    this.connection.getAndUpdate(conn -> {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (final Exception e) {
        LOG.error("deactivation failed: ", e);
      }
      return conn;
    });
  }
}
