<?php

it('1_2-3^4!', function () {
    $page = visit('/');
    $page->assertScreenshotMatches();
});
