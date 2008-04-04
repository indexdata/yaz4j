/** \file oclc-ill-req-ext.c
    \brief ASN.1 Module OCLCILLRequestExtension

    Generated automatically by YAZ ASN.1 Compiler 0.4
*/

#include <yaz/oclc-ill-req-ext.h>

int ill_OCLCILLRequestExtension (ODR o, ILL_OCLCILLRequestExtension **p, int opt, const char *name)
{
	if (!odr_sequence_begin (o, p, sizeof(**p), name))
		return odr_missing(o, opt, name) && odr_ok (o);
	return
		odr_explicit_tag (o, ill_String,
			&(*p)->clientDepartment, ODR_CONTEXT, 0, 1, "clientDepartment") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->paymentMethod, ODR_CONTEXT, 1, 1, "paymentMethod") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->uniformTitle, ODR_CONTEXT, 2, 1, "uniformTitle") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->dissertation, ODR_CONTEXT, 3, 1, "dissertation") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->issueNumber, ODR_CONTEXT, 4, 1, "issueNumber") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->volume, ODR_CONTEXT, 5, 1, "volume") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->affiliations, ODR_CONTEXT, 6, 1, "affiliations") &&
		odr_explicit_tag (o, ill_String,
			&(*p)->source, ODR_CONTEXT, 7, 1, "source") &&
		odr_sequence_end (o);
}

int ill_SystemNo_s (ODR o, ILL_SystemNo_s **p, int opt, const char *name)
{
	if (!odr_sequence_begin (o, p, sizeof(**p), name))
		return odr_missing(o, opt, name) && odr_ok (o);
	return
		ill_System(o, &(*p)->System, 0, "System") &&
		ill_RecordNo(o, &(*p)->recordNo, 0, "recordNo") &&
		odr_sequence_end (o);
}

int ill_SystemNo (ODR o, ILL_SystemNo **p, int opt, const char *name)
{
	if (!odr_initmember (o, p, sizeof(**p)))
		return odr_missing(o, opt, name);
	if (odr_sequence_of (o, (Odr_fun) ill_SystemNo_s, &(*p)->elements,
		&(*p)->num, name))
		return 1;
	if(o->direction == ODR_DECODE)
		*p = 0;
	return odr_missing(o, opt, name);
}

int ill_System (ODR o, ILL_System **p, int opt, const char *name)
{
	return odr_enum (o, p, opt, name);
}

int ill_RecordNo (ODR o, ILL_RecordNo **p, int opt, const char *name)
{
	return ill_String (o, p, opt, name);
}
