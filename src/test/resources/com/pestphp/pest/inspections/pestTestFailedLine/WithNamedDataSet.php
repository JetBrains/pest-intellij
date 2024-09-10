<?php

test("myTest", function(int $a) {
    <warning descr="Failed asserting that 1 is identical to 2."><warning descr="Failed asserting that 1 is identical to 3.">expect(1)->toBe($a);</warning></warning>
})->with("my dataset");

dataset("my dataset", ["first dataset" => [1], "second dataset" => [2], "third dataset" => [3]]);