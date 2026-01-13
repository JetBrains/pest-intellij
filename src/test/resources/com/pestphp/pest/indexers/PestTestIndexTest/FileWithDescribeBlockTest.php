<?php

describe('sum', function () {
    test('adds numbers', function () {
        expect(1 + 1)->toBe(2);
    });

    it('handles zero', function () {
        expect(0 + 0)->toBe(0);
    });
});
