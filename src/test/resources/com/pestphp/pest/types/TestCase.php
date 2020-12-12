<?php

namespace PHPUnit\Framework;

abstract class TestCase {
    protected $protectedField;

    /**
     * @param string $exception
     */
    public function expectException($exception) {}

    /**
     * @param int|string $code
     */
    public function expectExceptionCode($code){}

    protected function someProtectedMethod(){}

    public static function someStaticMethod(){}
}