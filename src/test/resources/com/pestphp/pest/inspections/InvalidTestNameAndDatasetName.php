<?php

dataset(name: 'someDataSet', dataset: []);

test(<weak_warning descr="Words in Pest test names should be separated by spaces">'NotSentenceCase'</weak_warning>, function () {
    expect(true)->toBeTrue();
})->with('someDataSet');