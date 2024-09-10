<?php

test("myTest", function () {
    <warning descr="Failed asserting that 1 is identical to 2.">myAssert(
        fn() => expect(1)->toBe(2)
    );</warning>
});

function myAssert(callable $a) {
    $a();
}