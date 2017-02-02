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

package com.io7m.thoth.command.system;

import com.io7m.thoth.command.api.ThothCommandType;
import javaslang.collection.List;
import org.osgi.service.component.annotations.Component;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * A command that shows how long the JVM has been running.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemUptime extends TCSystemCommand
{
  /**
   * Construct a command.
   */

  public TCSystemUptime()
  {

  }

  @Override
  public String name()
  {
    return "uptime";
  }

  @Override
  public List<String> execute(
    final List<String> text)
  {
    return List.of(uptime(ManagementFactory.getRuntimeMXBean().getUptime()));
  }

  private static String uptime(
    final long millis)
  {
    long current = millis;

    final long days = TimeUnit.MILLISECONDS.toDays(current);
    current -= TimeUnit.DAYS.toMillis(days);
    final long hours = TimeUnit.MILLISECONDS.toHours(current);
    current -= TimeUnit.HOURS.toMillis(hours);
    final long minutes = TimeUnit.MILLISECONDS.toMinutes(current);
    current -= TimeUnit.MINUTES.toMillis(minutes);
    final long seconds = TimeUnit.MILLISECONDS.toSeconds(current);

    final StringBuilder sb = new StringBuilder(64);
    sb.append(days);
    sb.append(" days ");
    sb.append(hours);
    sb.append(" hours ");
    sb.append(minutes);
    sb.append(" minutes ");
    sb.append(seconds);
    sb.append(" seconds");
    return sb.toString();
  }
}
