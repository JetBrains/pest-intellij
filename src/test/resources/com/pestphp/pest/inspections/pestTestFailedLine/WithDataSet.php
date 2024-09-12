<?php

test("myTest", function(int $a) {
    expect(1)->toBe($a);
})->with([[1], [2], [3]]);