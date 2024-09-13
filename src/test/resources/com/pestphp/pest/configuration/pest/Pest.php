<?php

class BaseTestCase
{
    public function baseTestFunc(){}
}

class FeatureTestCase
{
    public function featureTestFunc(){}
}

class SomeBaseTrait
{
    public function someBaseTraitFunc(){}
}

pest()->extend(BaseTestCase::class);

pest()->extend(FeatureTestCase::class)->in("Feature");

pest()->extend(FeatureTestCase::class, SomeBaseTrait::class)->group("some group")->in("GroupedFeature");

pest()->extend(FeatureTestCase::class)->in(__DIR__ . "/DIRFeature");

pest()->extend(FeatureTestCase::class)->in('../tests/DynamicFeature');