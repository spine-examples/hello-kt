/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.spine.helloworld.client

import com.google.common.collect.ImmutableSet
import io.spine.base.EventMessage
import io.spine.client.Client
import io.spine.client.Subscription
import io.spine.helloworld.hello.command.Print
import io.spine.helloworld.hello.event.Printed
import io.spine.json.Json
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * A simple client that sends the [Print] command to the Hello server, subscribes to
 * the resulting events, and prints them as they arrive.
 */
internal class Client(serverName: String) {

    /** The connection to the server. */
    private val client: Client = Client.inProcess(serverName)
        .shutdownTimeout(2, TimeUnit.SECONDS)
        .build()

    /** Subscriptions to the outcome of the sent command. */
    private var subscriptions: ImmutableSet<Subscription>? = null

    /** Sends the [Print] command to the server subscribing to resulting event. */
    fun sendCommand() {
        val commandMessage = with(Print.newBuilder()) {
            username = System.getProperty("user.name")
            text = "Hello World!"
            vBuild()
        }
        subscriptions = client.asGuest()
            .command(commandMessage)
            .observe(Printed::class.java) { event: Printed -> onPrinted(event) }
            .post()
    }

    /**
     * Prints the passed event and clears the subscriptions.
     *
     * @implNote Since we expect only one event produced in response to the [Print]
     * command we clear the subscriptions as the event arrives.
     */
    private fun onPrinted(event: Printed) {
        printEvent(event)
        cancelSubscriptions()
    }

    /** Prints the JSON form of the passed event message to the console. */
    private fun printEvent(e: EventMessage) {
        println("The client received the event: `${e.javaClass.name}${Json.toCompactJson(e)}`.")
    }

    /** Cancels all current subscriptions, if any. */
    private fun cancelSubscriptions() {
        if (subscriptions != null) {
            subscriptions!!.forEach(Consumer { s: Subscription ->
                client.subscriptions().cancel(s)
            })
            subscriptions = null
        }
    }

    /** Tests if the client finished cancelling active subscriptions. */
    val isDone: Boolean
        get() = client.subscriptions().isEmpty

    /** Closes the client, performing all necessary cleanups. */
    fun close() {
        client.close()
    }
}
