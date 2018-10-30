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
import io.vavr.collection.SortedSet;

import java.util.Optional;

/**
 * A resolver that finds installed commands and listeners.
 */

public interface ThothResolverType
{
  /**
   * @return The available command groups
   */

  SortedSet<String> commandGroups();

  /**
   * @param group The command group
   *
   * @return The commands available within a group at the time of the call
   */

  SortedSet<String> commandGroupList(String group);

  /**
   * Find a command with the given group and name.
   *
   * @param group The group
   * @param name  The command name
   *
   * @return The command, if one exists
   */

  Optional<ThothCommandType> commandFind(
    String group,
    String name);

  /**
   * @return The available listener groups
   */

  SortedSet<String> listenerGroups();

  /**
   * @param group The listener group
   *
   * @return The listeners available within a group at the time of the call
   */

  SortedSet<String> listenerGroupList(String group);

  /**
   * Retrieve the current listeners.
   *
   * @return The currently available listeners
   */

  List<ThothListenerType> listeners();
}
