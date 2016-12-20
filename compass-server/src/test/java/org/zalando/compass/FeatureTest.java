package org.zalando.compass;

import org.junit.runner.RunWith;
import org.zuchini.junit.Zuchini;
import org.zuchini.junit.ZuchiniOptions;

@RunWith(Zuchini.class)
@ZuchiniOptions(featurePackages = "features", stepDefinitionPackages = "org.zalando.compass", reportIndividualSteps = true)
public class FeatureTest {

}
