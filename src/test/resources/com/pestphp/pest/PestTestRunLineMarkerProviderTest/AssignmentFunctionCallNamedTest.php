<?php

function test($description, $closure = null): \Pest\PendingCalls\TestCall {
}

$x = test("name", function(){
    expect(1)->toBe(1);
});