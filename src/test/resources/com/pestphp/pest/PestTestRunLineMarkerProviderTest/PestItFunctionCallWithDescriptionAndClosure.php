<?php

function it($description, $closure = null): \Pest\PendingCalls\TestCall {
}

it('basic', function () {
    $this->assertTrue(true);
});