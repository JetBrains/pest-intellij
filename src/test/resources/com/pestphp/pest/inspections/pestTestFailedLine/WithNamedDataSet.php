<?php

test("myTest", function(int $a) {
    expect(1)->toBe($a);
})->with("my dataset");

dataset("my dataset", [[1], [2], [3]]);