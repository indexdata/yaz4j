package org.yaz4j.exception;

import java.util.Hashtable;

public class Bib1Diagnostic {

    private final static Hashtable<Integer, String> errorCodes = new Hashtable<Integer, String>();

    static {
        errorCodes.put(1, "PermanentSystemError");
        errorCodes.put(2, "TemporarySystemError");
        errorCodes.put(3, "UnsupportedSearch");
        errorCodes.put(4, "TermsOnlyIncludesExclusionOrStopWords");
        errorCodes.put(5, "TooManyArgumentWords");
        errorCodes.put(6, "TooManyBooleanOperators");
        errorCodes.put(7, "TooManyTruncatedWords");
        errorCodes.put(8, "TooManyIncompleteSubfields");
        errorCodes.put(9, "TruncatedWordsTooShort");
        errorCodes.put(10, "InvalidFormatForRecordNumberInSearchTerm");
        errorCodes.put(11, "TooManyCharactersInSearchStatement");
        errorCodes.put(12, "TooManyRecordsRetrieved");
        errorCodes.put(13, "PresentRequestOutOfRange");
        errorCodes.put(14, "SystemErrorInPresentingRecords");
        errorCodes.put(15, "RecordNotAuthorizedToBeSentIntersystem");
        errorCodes.put(16, "RecordExceedsPreferredMessageSize");
        errorCodes.put(17, "RecordExceedsExceptionalRecordSize");
        errorCodes.put(18, "ResultSetNotSupportedAsASearchTerm");
        errorCodes.put(19, "OnlySingleResultSetAsSearchTermSupported");
        errorCodes.put(20, "OnlyAndingOfASingleResultSetAsSearchTerm");
        errorCodes.put(21, "ResultSetExistsAndReplaceIndicatorOff");
        errorCodes.put(22, "ResultSetNamingNotSupported");
        errorCodes.put(23, "SpecifiedCombinationOfDatabasesNotSupported");
        errorCodes.put(24, "ElementSetNamesNotSupported");
        errorCodes.put(25, "SpecifiedElementSetNameNotValidForSpecifiedDatabase");
        errorCodes.put(26, "OnlyGenericFormOfElementSetNameSupported");
        errorCodes.put(27, "ResultSetNoLongerExistsUnilaterallyDeletedByTarget");
        errorCodes.put(28, "ResultSetIsInUse");
        errorCodes.put(29, "OneOfTheSpecifiedDatabasesIsLocked");
        errorCodes.put(30, "SpecifiedResultSetDoesNotExist");
        errorCodes.put(31, "ResourcesExhaustedNoResultsAvailable");
        errorCodes.put(32, "ResourcesExhaustedUnpredictablePartialResultsAvailable");
        errorCodes.put(33, "ResourcesExhaustedValidSubsetOfResultsAvailable");
        errorCodes.put(100, "UnspecifiedError");
        errorCodes.put(101, "AccessControlFailure");
        errorCodes.put(102, "ChallengeRequiredCouldNotBeIssuedOperationTerminated");
        errorCodes.put(103, "ChallengeRequiredCouldNotBeIssuedRecordNotIncluded");
        errorCodes.put(104, "ChallengeFailedRecordNotIncluded");
        errorCodes.put(105, "TerminatedAtOriginRequest");
        errorCodes.put(106, "NoAbstractSyntaxesAgreedToForThisRecord");
        errorCodes.put(107, "QueryTypeNotSupported");
        errorCodes.put(108, "MalformedQuery");
        errorCodes.put(109, "DatabaseUnavailable");
        errorCodes.put(110, "OperatorUnsupported");
        errorCodes.put(111, "TooManyDatabasesSpecified");
        errorCodes.put(112, "TooManyResultSetsCreated");
        errorCodes.put(113, "UnsupportedAttributeType");
        errorCodes.put(114, "UnsupportedUseAttribute");
        errorCodes.put(115, "UnsupportedTermValueForUseAttribute");
        errorCodes.put(116, "UseAttributeRequiredButNotSupplied");
        errorCodes.put(117, "UnsupportedRelationAttribute");
        errorCodes.put(118, "UnsupportedStructureAttribute");
        errorCodes.put(119, "UnsupportedPositionAttribute");
        errorCodes.put(120, "UnsupportedTruncationAttribute");
        errorCodes.put(121, "UnsupportedAttributeSet");
        errorCodes.put(122, "UnsupportedCompletenessAttribute");
        errorCodes.put(123, "UnsupportedAttributeCombination");
        errorCodes.put(124, "UnsupportedCodedValueForTerm");
        errorCodes.put(125, "MalformedSearchTerm");
        errorCodes.put(126, "IllegalTermValueForAttribute");
        errorCodes.put(127, "UnparsableFormatForUnNormalizedValue");
        errorCodes.put(128, "IllegalResultSetName");
        errorCodes.put(129, "ProximitySearchOfSetsNotSupported");
        errorCodes.put(130, "IllegalResultSetInProximitySearch");
        errorCodes.put(131, "UnsupportedProximityRelation");
        errorCodes.put(132, "UnsupportedProximityUnitCode");
        errorCodes.put(201, "ProximityNotSupportedWithThisAttributeCombinationAttribute");
        errorCodes.put(202, "UnsupportedDistanceForProximity");
        errorCodes.put(203, "OrderedFlagNotSupportedForProximity");
        errorCodes.put(205, "OnlyZeroStepSizeSupportedForScan");
        errorCodes.put(206, "SpecifiedStepSizeNotSupportedForScanStep");
        errorCodes.put(207, "CannotSortAccordingToSequence");
        errorCodes.put(208, "NoResultSetNameSuppliedOnSort");
        errorCodes.put(209, "GenericSortNotSupported");
        errorCodes.put(210, "DatabaseSpecificSortNotSupported");
        errorCodes.put(211, "TooManySortKeys");
        errorCodes.put(212, "DuplicateSortKeys");
        errorCodes.put(213, "UnsupportedMissingDataAction");
        errorCodes.put(214, "IllegalSortRelation");
        errorCodes.put(215, "IllegalCaseValue");
        errorCodes.put(216, "IllegalMissingDataAction");
        errorCodes.put(217, "SegmentationCannotGuaranteeRecordsWillFitInSpecifiedSegments");
        errorCodes.put(218, "EsPackageNameAlreadyInUse");
        errorCodes.put(219, "EsNoSuchPackageOnModifyDelete");
        errorCodes.put(220, "EsQuotaExceeded");
        errorCodes.put(221, "EsExtendedServiceTypeNotSupported");
        errorCodes.put(222, "EsPermissionDeniedOnEsIdNotAuthorized");
        errorCodes.put(223, "EsPermissionDeniedOnEsCannotModifyOrDelete");
        errorCodes.put(224, "EsImmediateExecutionFailed");
        errorCodes.put(225, "EsImmediateExecutionNotSupportedForThisService");
        errorCodes.put(226, "EsImmediateExecutionNotSupportedForTheseParameters");
        errorCodes.put(227, "NoDataAvailableInRequestedRecordSyntax");
        errorCodes.put(228, "ScanMalformedScan");
        errorCodes.put(229, "TermTypeNotSupported");
        errorCodes.put(230, "SortTooManyInputResults");
        errorCodes.put(231, "SortIncompatibleRecordFormats");
        errorCodes.put(232, "ScanTermListNotSupported");
        errorCodes.put(233, "ScanUnsupportedValueOfPositionInResponse");
        errorCodes.put(234, "TooManyIndexTermsProcessed");
        errorCodes.put(235, "DatabaseDoesNotExist");
        errorCodes.put(236, "AccessToSpecifiedDatabaseDenied");
        errorCodes.put(237, "SortIllegalSort");
        errorCodes.put(238, "RecordNotAvailableInRequestedSyntax");
        errorCodes.put(239, "RecordSyntaxNotSupported");
        errorCodes.put(240, "ScanResourcesExhaustedLookingForSatisfyingTerms");
        errorCodes.put(241, "ScanBeginningOrEndOfTermList");
        errorCodes.put(242, "SegmentationMaxSegmentSizeTooSmallToSegmentRecord");
        errorCodes.put(243, "PresentAdditionalRangesParameterNotSupported");
        errorCodes.put(244, "PresentCompSpecParameterNotSupported");
        errorCodes.put(245, "Type1QueryRestrictionOperandNotSupported");
        errorCodes.put(246, "Type1QueryComplexAttributevalueNotSupported");
        errorCodes.put(247, "Type1QueryAttributesetAsPartOfAttributeelementNotSupported");
    }

    public static String getError(int errorCode) {
        String errorText = "Unknown Error";

        if (errorCodes.containsKey(errorCode)) {
            errorText = errorCodes.get(errorCode);
        }

        return errorText;
    }
}
