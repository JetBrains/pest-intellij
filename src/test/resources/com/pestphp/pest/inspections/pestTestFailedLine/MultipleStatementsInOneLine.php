<?php

test("myTest", function() {
    <warning descr="Failed asserting that 1 is identical to 2.">expect(1)->toBe(1); expect(1)->toBe(2);</warning>
});