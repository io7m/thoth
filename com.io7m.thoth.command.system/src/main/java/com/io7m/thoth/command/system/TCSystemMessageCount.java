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
import com.io7m.thoth.command.api.ThothListenerType;
import com.io7m.thoth.command.api.ThothResponse;
import javaslang.collection.List;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.math.BigInteger;

/**
 * A listener and command pair that keep track of the number of messages
 * received since the bot started.
 */

@Component(immediate = true, service = {ThothListenerType.class, ThothCommandType.class})
public final class TCSystemMessageCount extends TCSystemListener implements
  ThothCommandType
{
  private BigInteger count;

  /**
   * Construct a command.
   */

  public TCSystemMessageCount()
  {

  }

  @Override
  public String name()
  {
    return "message-count";
  }

  /**
   * Activate the listener, setting the count to zero.
   */

  @Activate
  public void onActivate()
  {
    this.count = BigInteger.ZERO;
  }

  @Override
  public List<ThothResponse> receive(
    final String text)
  {
    this.count = this.count.add(BigInteger.ONE);
    return List.empty();
  }

  @Override
  public List<ThothResponse> execute(
    final List<String> text)
  {
    return List.of(ThothResponse.of(this.count.toString()));
  }
}
