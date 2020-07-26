<?php

class Foo {
    public int $a;

    public function b(){}
}

beforeEach(function () {
    $this->foo = new Foo();
});

afterEach('has home', function () {
    $this->foo-><caret>
});
