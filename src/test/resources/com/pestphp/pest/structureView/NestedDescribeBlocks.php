<?php

describe('SomeClass', function () {
    it('is visible as nested', function () {
        expect(true)->toBeTrue();
    });

    describe('SomeMethod', function () {
        it('is visible as twice nested', function () {
            expect(true)->toBeTrue();
        });
    });
});
