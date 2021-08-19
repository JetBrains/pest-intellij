<?php

namespace Tests\Works;

class Foo {
    public int $a;

    public function b(){}
}

beforeEach(function () {
    $this->foo = new Foo();
});

it('has home', function () {
    $this->foo-><caret>
});
