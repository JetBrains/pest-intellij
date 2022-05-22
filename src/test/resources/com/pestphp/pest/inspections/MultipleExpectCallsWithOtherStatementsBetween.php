<?php

<weak_warning descr="Multiple expect can be converted to chainable calls.">expect(Earth::owner())->toBeInstanceOf(Mouse::class);</weak_warning>
<weak_warning descr="Multiple expect can be converted to chainable calls.">expect($answer)->toBe(42);</weak_warning>
Earth::tryCalculateQuestion();
<weak_warning descr="Multiple expect can be converted to chainable calls.">expect($question)->toBeNull();</weak_warning>
<weak_warning descr="Multiple expect can be converted to chainable calls.">expect(Earth::exists())->toBeFalse();</weak_warning>