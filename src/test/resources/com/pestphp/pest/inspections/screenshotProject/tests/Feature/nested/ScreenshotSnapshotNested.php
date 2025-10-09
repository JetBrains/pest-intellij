<?php

test('nested', function () {
    $page = visit('/');
    $page->assertScreenshotMatches();
});
