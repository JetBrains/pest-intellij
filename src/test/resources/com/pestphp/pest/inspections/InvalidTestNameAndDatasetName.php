<?php

dataset(name: 'someDataSet', dataset: []);

test(<weak_warning descr="Words in Pest test names must be separated by spaces">'NotSentenceCase'</weak_warning>, function () {
    expect(true)->toBeTrue();
})->with('someDataSet');