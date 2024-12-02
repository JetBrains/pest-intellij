<?php

namespace Pest\PendingCalls {
    class TestCall {
        public function with(Closure|iterable|string ...$data): self
    }
}

function it(string $description, ?Closure $closure = null): Pest\PendingCalls\TestCall {
}

function describe(string $description, $tests): \Pest\PendingCalls\TestCall {
}

describe("a set of tests", function() {
    it('can run individual data sets', function (int $a, int $b) {
        //...
    })->with([
           "dataset a" => [3, 3],
           "dataset b" => [4, 4]
         ]);
    it('Can act as summator', function ($a) {
        $result = (new SimpleCalculator)->add($a, b: 0);
        expect ($result) ->toBe ( expected: 1);
    }) ->with([
        'first' => fn() => array_map (fn() => rand(1,10), range( start: 1, end: 10)) ,
        'second' => [1],
    ]);
});
