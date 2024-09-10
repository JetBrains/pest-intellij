<?php

test("myTest", function() {
    expect(1)->toBe(1);
    <warning descr="Failed asserting that 1 is identical to 2.">expect(1)->toBe(2);</warning>
    expect(2)->toBe(2);
});