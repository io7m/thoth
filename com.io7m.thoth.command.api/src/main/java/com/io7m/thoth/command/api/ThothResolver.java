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
import io.vavr.collection.SortedMap;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeMap;
import io.vavr.collection.TreeSet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The default implementation of the {@link ThothResolverType} interface.
 */

@Component
public final class ThothResolver implements ThothResolverType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ThothResolver.class);
  }

  private SortedMap<String, SortedMap<String, ThothCommandType>> commands;
  private SortedMap<String, SortedMap<String, ThothListenerType>> listeners;

  /**
   * Create an empty resolver.
   */

  public ThothResolver()
  {
    this.commands = TreeMap.empty();
    this.listeners = TreeMap.empty();
  }

  /**
   * Register a command.
   *
   * @param command The command
   */

  @Reference(
    unbind = "onCommandDeregister",
    cardinality = ReferenceCardinality.MULTIPLE,
    policyOption = ReferencePolicyOption.GREEDY,
    policy = ReferencePolicy.DYNAMIC)
  public synchronized void onCommandRegister(
    final ThothCommandType command)
  {
    final String group_name = command.group();
    final SortedMap<String, ThothCommandType> group;
    if (this.commands.containsKey(command.group())) {
      group = this.commands.get(command.group()).get();
    } else {
      group = TreeMap.empty();
    }

    final String name = command.name();
    if (group.containsKey(name)) {
      LOG.warn("command {}:{} is already registered", group_name, name);
      return;
    }

    LOG.debug("registered command {}:{}", group_name, name);
    this.commands = this.commands.put(group_name, group.put(name, command));
  }

  /**
   * Deregister a command.
   *
   * @param command The command
   */

  public synchronized void onCommandDeregister(
    final ThothCommandType command)
  {
    final String group_name = command.group();
    final String name = command.name();
    final SortedMap<String, ThothCommandType> group;
    if (this.commands.containsKey(command.group())) {
      group = this.commands.get(command.group()).get();
    } else {
      LOG.warn("command {}:{} is not registered", group_name, name);
      return;
    }

    if (!group.containsKey(name)) {
      LOG.warn("command {}:{} is not registered", group_name, name);
      return;
    }

    LOG.debug("unregistered command {}:{}", group_name, name);
    this.commands = this.commands.put(group_name, group.remove(name));
  }

  /**
   * Register a listener.
   *
   * @param listener The listener
   */

  @Reference(
    unbind = "onListenerDeregister",
    cardinality = ReferenceCardinality.MULTIPLE,
    policyOption = ReferencePolicyOption.GREEDY,
    policy = ReferencePolicy.DYNAMIC)
  public synchronized void onListenerRegister(
    final ThothListenerType listener)
  {
    final String group_name = listener.group();
    final SortedMap<String, ThothListenerType> group;
    if (this.listeners.containsKey(listener.group())) {
      group = this.listeners.get(listener.group()).get();
    } else {
      group = TreeMap.empty();
    }

    final String name = listener.name();
    if (group.containsKey(name)) {
      LOG.warn("listener {}:{} is already registered", group_name, name);
      return;
    }

    LOG.debug("registered listener {}:{}", group_name, name);
    this.listeners = this.listeners.put(group_name, group.put(name, listener));
  }

  /**
   * Deregister a listener.
   *
   * @param listener The listener
   */

  public synchronized void onListenerDeregister(
    final ThothListenerType listener)
  {
    final String group_name = listener.group();
    final String name = listener.name();
    final SortedMap<String, ThothListenerType> group;
    if (this.listeners.containsKey(listener.group())) {
      group = this.listeners.get(listener.group()).get();
    } else {
      LOG.warn("listener {}:{} is not registered", group_name, name);
      return;
    }

    if (!group.containsKey(name)) {
      LOG.warn("listener {}:{} is not registered", group_name, name);
      return;
    }

    LOG.debug("unregistered listener {}:{}", group_name, name);
    this.listeners = this.listeners.put(group_name, group.remove(name));
  }

  @Override
  public SortedSet<String> commandGroups()
  {
    return this.commands.keySet();
  }

  @Override
  public SortedSet<String> commandGroupList(final String group)
  {
    return this.commands.get(group)
      .map(names -> names.keySet().map(name -> group + ":" + name))
      .getOrElse(TreeSet.empty());
  }

  @Override
  public Optional<ThothCommandType> commandFind(
    final String group,
    final String name)
  {
    return this.commands.get(group)
      .flatMap(group_commands -> group_commands.get(name))
      .toJavaOptional();
  }

  @Override
  public SortedSet<String> listenerGroups()
  {
    return this.listeners.keySet();
  }

  @Override
  public SortedSet<String> listenerGroupList(
    final String group)
  {
    return this.listeners.get(group)
      .map(names -> names.keySet().map(name -> group + ":" + name))
      .getOrElse(TreeSet.empty());
  }

  @Override
  public List<ThothListenerType> listeners()
  {
    return this.listeners.values()
      .foldRight(List.empty(), (pair, list) -> list.appendAll(pair.values()));
  }
}
