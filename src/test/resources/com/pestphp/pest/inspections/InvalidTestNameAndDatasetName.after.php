<?php

dataset(name: 'someDataSet', dataset: []);

test('Not Sentence Case', function () {
    expect(true)->toBeTrue();
})->with('someDataSet');