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

package com.io7m.thoth.command.judges;

import com.io7m.thoth.command.api.ThothCommandType;
import javaslang.collection.List;
import org.osgi.service.component.annotations.Component;

/**
 * <p>A command that displays ASCII judges.</p>
 *
 * {@code .o/[8.3] .o/[6.4] .o/[6.6] .o/[0.1]}
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCJudgesScore extends TCJudgesCommand
{
  /**
   * Create a command.
   */

  public TCJudgesScore()
  {

  }

  @Override
  public String name()
  {
    return "score";
  }

  @Override
  public List<String> execute(
    final List<String> text)
  {
    final double judge0 = 10.0 - (Math.random() * 5.0);
    final double judge1 = 10.0 - (Math.random() * 5.0);
    final double judge2 = 10.0 - (Math.random() * 5.0);
    final double judge3 = (Math.random() * 10.0) * Math.random();

    return List.of(String.format(
      ".o/[%.1f] .o/[%.1f] .o/[%.1f] .o/[%.1f]",
      Double.valueOf(judge0),
      Double.valueOf(judge1),
      Double.valueOf(judge2),
      Double.valueOf(judge3)));
  }
}
