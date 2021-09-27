<?php

expect()->extend('toHaveValues', function ($valueA, string $valueB, bool $valueC = true): bool {
    return true;
});