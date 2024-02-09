<?php

function test($description = null, $closure = null): \Pest\PendingCalls\TestCall {
}

test("foo", function () {
    test();
    test()->createMock("");
});