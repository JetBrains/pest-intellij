<?php

test("myTest", function () {
    <warning descr="Failed asserting that 1 is identical to 2.">myAssert(function () {
        echo 1;
        expect(1)->toBe(2);
    });</warning>
});

function myAssert(callable $a) {
    $a();
}