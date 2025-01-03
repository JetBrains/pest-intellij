<?php

describe('SomeClass', function () {
    it('works as well', function () {
        expect(true)->toBeTrue();
    });

    describe('SomeMethod', function () {
        it('does not work', function () {
            expect(true)->toBeTrue();
        });
    });
});