<?php

class A {
}

test(A::class, function () {
    $this->assertTrue(true);
});
