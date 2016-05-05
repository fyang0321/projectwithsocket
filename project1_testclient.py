#!/usr/bin/python

import sys
import time
import urllib2
from urllib2 import urlopen,HTTPError

TIMEOUT = 5
NORMAL_FILES = [['/index.html', 'text/html'],
				[ '/foo/bar.html', 'text/html'],
				[ '/images/uchicago/logo.png', 'image/png']]
REDIRECTS = [['/nyt', 'http://www.nytimes.com/']]
NOTFOUNDS = ['/redirect.defs', '/not/a/real/url.html']

# prevents us from following redirects, so we can get the real response code
class DontFollowHttpRedirectHandler(urllib2.HTTPRedirectHandler):
	def http_error_307(self, req, fp, code, msg, headers):
		raise urllib2.HTTPError(req.get_full_url(), code, msg, headers, fp)
	http_error_302 = http_error_303 = http_error_307


def build_url(host, port, filename):
	return 'http://' + host + ':' + port + filename

# reads file contents from disk, so we can compare with what the server returned.
def read_file(url):
	f = open('www' + url)
	return f.readlines()

# returns request, response, code
# response is None for 4xx or 5xx response
def maybe_fetch(host, port, filename, op='GET'):
	request = urllib2.Request(build_url(host, port, filename))
	request.get_method = lambda : op
	opener = urllib2.build_opener(DontFollowHttpRedirectHandler)
	response = None
	try:
		response = opener.open(request)
	except HTTPError as e:
		return request, None, e.code
	return request, response, response.code

def test_POST(host, port):
	print 'test POST (unsupported)'
	for filename, mime_type in NORMAL_FILES:
		request, response, code = maybe_fetch(host, port, filename, op='POST')
		if code != 403:
			return 'FAIL'
	return 'PASS'

def test_INVALID(host, port):
	print 'test INVALID'
	for filename, mime_type in NORMAL_FILES:
		request, response, code = maybe_fetch(host, port, filename, op='FOOBAR')
		if code != 403 and code < 500:
			return 'FAIL'
	return 'PASS'

def test_200(host, port, opcode='GET'):
	print 'test 200s ' + opcode

	for filename, mime_type in NORMAL_FILES:
		print '\t%s' % (filename)
		request, response, code = maybe_fetch(host, port, filename, op=opcode)
		if code != 200 or response is None:
			return 'FAIL'
		if opcode=='HEAD' and len(response.readlines()) != 0:
			return 'FAIL'
		if opcode=='GET' and (response.readlines() != read_file(filename)):
			print 'Got different bytes back from server than read from local disk!'
			return 'FAIL'
		if mime_type != response.info().getheader('Content-Type'):
			print 'Unexpected MIME type ' + response.info().getheader('Content-Type') \
				+ ' for URL ' + filename
			return 'FAIL'
	return 'PASS'

def test_404(host, port, opcode='GET'):
	print 'test 404s ' + opcode
	for filename in NOTFOUNDS:
		print '\t%s' % (filename)
		request, response, code = maybe_fetch(host, port, filename, op=opcode)
		if code != 404 or response != None:
			return 'FAIL'
	return 'PASS'

def test_301(host, port, opcode='GET'):
	print 'test 301s ' + opcode

	for filename, redirect in REDIRECTS:
		print '\t%s--->%s' % (filename, redirect)
		request, response, code = maybe_fetch(host, port, filename, op=opcode)
		# because we followed the redirect, the code should actually be 200,
		# and there should be a response.
		if code != 200 or response == None:
			return 'FAIL'
		if response.url != redirect:
			return 'FAIL'
	return 'PASS'
	
def parse_flags(argv):
	arg_map = {}
	if len(argv) <= 1: return {}
	for arg in argv[1:]:
		bits = arg.split('=')
		if len(bits) == 2:
			arg_map[bits[0]] = bits[1]
	return arg_map
		
if __name__  == '__main__':
	arg_map = parse_flags(sys.argv)
	if ('--host' not in arg_map) or ('--port' not in arg_map):
		print 'usage: project1_testclient.py --host=linux2 --port=12345'
		sys.exit(-1)
	host = arg_map['--host']
	port = arg_map['--port']	

	n_failed = 0

	result_200 = test_200(host, port)
	print result_200 + '\n'
	if result_200 == 'FAILED':
		n_failed = n_failed + 1

	result_404 = test_404(host, port)
	print result_404 + '\n'
	if result_404 == 'FAILED':
		n_failed = n_failed + 1

	result_301 = test_301(host, port)
	print result_301 + '\n'	
	if result_301 == 'FAILED':
		n_failed = n_failed + 1

	head_200 = test_200(host, port, opcode='HEAD')
	print head_200 + '\n'
	if head_200 == 'FAILED':
		n_failed = n_failed + 1

	head_404 = test_404(host, port, opcode='HEAD')
	print head_404 + '\n'
	if head_404 == 'FAILED':
		n_failed = n_failed + 1

	head_301 = test_301(host, port, opcode='HEAD')
	print head_301 + '\n'
	if head_301 == 'FAILED':
		n_failed = n_failed + 1

	result_post = test_POST(host, port)
	print result_post + '\n'
	if result_post == 'FAILED':
		n_failed = n_failed + 1

	result_invalid = test_INVALID(host, port)
	print result_invalid + '\n'
	if result_invalid == 'FAILED':
		n_failed = n_failed + 1

	if n_failed > 0:
		print '\n!!!!! Failing tests; see above. !!!!!\n'
	else:
		print '\n***** All tests passed! *****\n'

