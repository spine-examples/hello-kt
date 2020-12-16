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
package io.spine.helloworld

import com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly
import io.spine.base.Identifier
import io.spine.helloworld.client.Client
import io.spine.helloworld.server.Server
import java.io.IOException
import java.time.Duration

/**
 * This example application demonstrates sending a command to a server, observing the results
 * of the handling of the command.
 *
 * This app consists of the two parts:
 *  1. In-process server configured to serve the Hello Context.
 *  2. A client connected to the in-process server.
 *
 * In a real world scenario these parts would be implemented by separate applications.
 *
 * The client:
 *  1. generates a [Print][io.spine.helloworld.hello.command.Print] command;
 *  2. subscribes to [Printed][io.spine.helloworld.hello.event.Printed] events that would be
 *     generated in response to the command;
 *  3. posts the command to the server.
 *
 * After the command is posted and handled, the application terminates, closing the client
 * and the server.
 *
 * @see Server
 * @see Client
 */
@Suppress("UnstableApiUsage") // `sleepUninterruptibly()` is @Beta. OK for this example.
fun main() {
    val serverName = Identifier.newUuid()
    val server = Server(serverName)
    var client: Client? = null
    try {
        server.start()
        client = Client(serverName)
        client.sendCommand()
        while (!client.isDone) {
            sleepUninterruptibly(Duration.ofMillis(100))
        }
    } catch (e: IOException) {
        onError(e)
    } finally {
        client?.close()
        server.shutdown()
    }
}

/**
 * Prints a stack trace of the passed exception.
 *
 * A real app should use more sophisticated exception handling.
 */
private fun onError(e: Exception) {
    e.printStackTrace()
}
