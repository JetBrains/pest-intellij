<?php

function test($description, $closure = null): \Pest\PendingCalls\TestCall {
}

function describe(string $description, $tests): \Pest\PendingCalls\TestCall {
}

describe("block", function () {
    test("foo", function () {
    });
});