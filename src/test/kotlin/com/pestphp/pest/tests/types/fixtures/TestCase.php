<?php

namespace PHPUnit\Framework;

abstract class TestCase {
    /**
     * @param string $exception
     */
    public function expectException($exception) {}

    /**
     * @param int|string $code
     */
    public function expectExceptionCode($code){}
}