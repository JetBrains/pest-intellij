<?php

dataset('numbers', [1, 2, 3]);

describe('math', function () {
    it('can add numbers', function (int $number) {
        expect($number)->toBeInt();
    })->with(<error descr="The dataset does not exist">'invalid_dataset'</error>);
});
