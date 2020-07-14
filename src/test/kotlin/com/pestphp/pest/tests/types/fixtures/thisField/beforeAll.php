<?php

class Foo {
    public $a;

    public function b(){}
}

\beforeAll(fn() => $this->foo = new Foo());

it('has home', function () {
    $this->foo-><caret>
});
