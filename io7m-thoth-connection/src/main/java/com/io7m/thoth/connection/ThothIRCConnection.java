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

package com.io7m.thoth.connection;

import com.io7m.jnull.NullCheck;
import com.io7m.thoth.command.api.ThothCommandType;
import com.io7m.thoth.command.api.ThothResolverType;
import javaslang.collection.List;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * The default implementation of the {@link ThothIRCConnectionType} interface.
 */

public final class ThothIRCConnection implements ThothIRCConnectionType
{
  private static final Logger LOG;
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  static {
    LOG = LoggerFactory.getLogger(ThothIRCConnection.class);
  }

  private final ThothListener listener;
  private Configuration configuration;
  private PircBotX bot;

  private ThothIRCConnection(
    final ThothIRCConnectionConfiguration in_config,
    final ThothResolverType in_resolver)
  {
    this.listener = new ThothListener(in_config, in_resolver);
  }

  /**
   * Create a new connection.
   *
   * @param config   The connection configuration
   * @param resolver A command/listener resolver
   *
   * @return A new connection
   */

  public static ThothIRCConnectionType create(
    final ThothIRCConnectionConfiguration config,
    final ThothResolverType resolver)
  {
    NullCheck.notNull(config, "Config");
    NullCheck.notNull(resolver, "Resolver");

    final UtilSSLSocketFactory tls_factory = new UtilSSLSocketFactory();
    tls_factory.trustAllCertificates();

    final Configuration.Builder cb = new Configuration.Builder();
    cb.setName(config.user());
    cb.setLogin(config.user());
    cb.setVersion(makePackage());
    cb.setSocketFactory(tls_factory);
    cb.addServer(config.address().getHostName(), config.address().getPort());
    cb.addAutoJoinChannel(config.channel());
    cb.setAutoReconnect(true);
    cb.setAutoReconnectAttempts(Integer.MAX_VALUE);
    cb.setMessageDelay(10L);

    final ThothIRCConnection conn = new ThothIRCConnection(config, resolver);
    cb.addListener(conn.listener);

    final Configuration c = cb.buildConfiguration();
    conn.setConfiguration(c);
    final PircBotX bot = new PircBotX(c);
    conn.setBot(bot);
    return conn;
  }

  private static String makePackage()
  {
    final Package pack = ThothIRCConnection.class.getPackage();
    final StringBuilder s = new StringBuilder(32);
    s.append("thoth ");
    s.append(pack.getImplementationVersion());
    return s.toString();
  }

  private void setConfiguration(
    final Configuration in_config)
  {
    this.listener.setConfiguration(in_config);
  }

  private void setBot(
    final PircBotX in_bot)
  {
    this.bot = NullCheck.notNull(in_bot, "Bot");
    this.listener.setBot(in_bot);
  }

  @Override
  public void start()
    throws IOException, IrcException
  {
    LOG.debug("starting");
    this.bot.startBot();
    LOG.debug("stopped");
  }

  @Override
  public void close()
    throws Exception
  {
    LOG.debug("sending stop request");
    this.bot.stopBotReconnect();
    this.bot.close();
    LOG.debug("stopped request sent");
  }

  private final class ThothListener extends ListenerAdapter
  {
    private final ThothIRCConnectionConfiguration connection_config;
    private final ThothResolverType resolver;
    private Configuration configuration;
    private PircBotX bot;
    private String command_prefix;

    private ThothListener(
      final ThothIRCConnectionConfiguration in_config,
      final ThothResolverType in_resolver)
    {
      this.connection_config = NullCheck.notNull(in_config, "Config");
      this.resolver = NullCheck.notNull(in_resolver, "Resolver");
      this.command_prefix = this.connection_config.user() + ": ";
    }

    private void setConfiguration(
      final Configuration in_config)
    {
      this.configuration = NullCheck.notNull(in_config, "Configuration");
    }

    private void setBot(
      final PircBotX in_bot)
    {
      this.bot = NullCheck.notNull(in_bot, "Bot");
    }

    @Override
    public void onMessage(
      final MessageEvent event)
      throws Exception
    {
      final String text = event.getMessage();
      if (text.startsWith(this.command_prefix)) {
        final String command_text =
          text.substring(this.command_prefix.length()).trim();
        this.runMessage(command_text, event::respond);
      }

      this.resolver.listeners().forEach(x -> x.receive(text));
    }

    @Override
    public void onPrivateMessage(
      final PrivateMessageEvent event)
      throws Exception
    {
      final String text = event.getMessage();
      this.runMessage(text, event::respondPrivateMessage);
      this.resolver.listeners().forEach(x -> x.receive(text));
    }

    private void runMessage(
      final String command_text,
      final Consumer<String> responder)
    {
      final List<String> segments = List.of(WHITESPACE.split(command_text));
      if (segments.size() > 0) {
        final String qualified = segments.head();
        final List<String> command_pieces = List.of(qualified.split(":"));
        if (command_pieces.size() == 2) {
          final String group = command_pieces.get(0);
          final String name = command_pieces.get(1);
          this.runCommand(segments, qualified, group, name, responder);
        } else {
          LOG.debug("could not parse command pieces");
        }
      } else {
        LOG.debug("empty message");
      }
    }

    private void runCommand(
      final List<String> segments,
      final String qualified,
      final String group,
      final String name,
      final Consumer<String> respond)
    {
      LOG.debug("resolving command {}:{}", group, name);
      final Optional<ThothCommandType> command =
        this.resolver.commandFind(group, name);
      if (command.isPresent()) {
        final List<String> lines =
          command.get().execute(segments.tail());
        lines.forEach(respond);
      } else {
        respond.accept("Unknown command: " + qualified);
      }
    }

    @Override
    public void onJoin(
      final JoinEvent event)
      throws Exception
    {
      final User user = event.getUser();
      final boolean me = user.getLogin().equals(this.configuration.getLogin());
      if (me) {
        this.bot.send().message(
          this.connection_config.channel(),
          makePackage());
      }
    }
  }
}
