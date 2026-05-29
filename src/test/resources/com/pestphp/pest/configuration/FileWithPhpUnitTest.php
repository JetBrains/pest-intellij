<?php

class FileWithPhpUnitTest extends \PHPUnit\Framework\TestCase
{
    public function testFoo()
    {
        <caret>
        $this->assertTrue(true);
    }
}
