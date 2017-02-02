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
import java.lang.management.OperatingSystemMXBean;

/**
 * A command that displays information about the host system.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemHost extends TCSystemCommand
{
  /**
   * Construct a command.
   */

  public TCSystemHost()
  {

  }

  @Override
  public String name()
  {
    return "host";
  }

  @Override
  public List<String> execute(
    final List<String> text)
  {
    final OperatingSystemMXBean os =
      ManagementFactory.getOperatingSystemMXBean();

    return List.of(String.format(
      "%s %s %s, %d cores, load average %f",
      os.getName(),
      os.getArch(),
      os.getVersion(),
      Integer.valueOf(os.getAvailableProcessors()),
      Double.valueOf(os.getSystemLoadAverage())));
  }
}
