<?php

describe('sum', function () {
    <error descr="Pest test names must be unique within a file">test('adds numbers', function () {
        expect(1 + 1)->toBe(2);
    })</error>;

    <error descr="Pest test names must be unique within a file">test('adds numbers', function () {
        expect(2 + 2)->toBe(4);
    })</error>;
});
