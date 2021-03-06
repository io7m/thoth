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

package com.io7m.thoth.command.api;

import io.vavr.collection.List;

import java.util.UUID;

/**
 * <p>The type of commands.</p>
 *
 * <p>A command is an explicitly parsed request directed at the bot from a
 * single user. The responses, if any, are addressed to the sender. The command
 * receives a list of arguments produced by splitting the original message into
 * tokens based on whitespace.</p>
 */

public interface ThothCommandType
{
  /**
   * @return The command group, for namespacing purposes
   */

  String group();

  /**
   * @return The command name, for namespacing purposes
   */

  String name();

  /**
   * Execute the command with the given list of arguments.
   *
   * @param text The arguments
   *
   * @return A list of messages with which to respond
   *
   * @deprecated Use {@link #executeCommand(ThothCommandParsed)}
   */

  @Deprecated
  default List<ThothResponse> execute(
    final List<String> text)
  {
    return this.executeCommand(ThothCommandParsed.of(
      text,
      "nobody",
      "nobody",
      UUID.fromString("1b2abff8-ae28-4b5a-bd6c-491f64a2752c")));
  }

  /**
   * Execute the given parsed command.
   *
   * @param command The parsed command
   *
   * @return A list of messages with which to respond
   */

  default List<ThothResponse> executeCommand(
    final ThothCommandParsed command)
  {
    return this.execute(command.arguments());
  }
}
