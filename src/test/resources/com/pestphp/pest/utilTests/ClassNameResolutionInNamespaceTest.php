<?php

namespace A {
    class B {
    }
}

use A;

test(B::class, function () {
    $this->assertTrue(true);
});
